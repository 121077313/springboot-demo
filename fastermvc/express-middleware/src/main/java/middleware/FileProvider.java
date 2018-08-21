package middleware;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import express.http.Middleware;
import express.http.Request;
import express.http.Response;
import express.utils.MediaType;
import express.utils.Status;
import express.utils.Utils;

/**
 * @author Simon Reinisch An middleware to provide access to static
 *         server-files.
 */
public  class FileProvider extends Middleware {

	public enum DotFiles {

		IGNORE, DENY, ALLOW

	}

	private final Logger logger;
	private FileProviderOptions options;
	private String root;

	{
		this.logger = Logger.getLogger(this.getClass().getSimpleName());
		this.logger.setUseParentHandlers(false); // Disable default console log
	}
	public FileProvider() {
		
	}
	public FileProvider(String root) {

		this(root, new FileProviderOptions());
	}

	public FileProvider(String root, FileProviderOptions options) {
		Path rootDir = Paths.get(root);
//		Resource res = new ClassPathResource(root);

//		URL url = FileProvider.class.getClassLoader().getSystemResource(root);

//		Path rootDir = null;
		try {
//			rootDir = Paths.get(res.getURI());
			if (!Files.exists(rootDir) || !Files.isDirectory(rootDir)) {

				throw new IOException(rootDir + " does not exists or isn't an directory.");

			}

//			if (!res.exists()) {
//				throw new IOException(rootDir + " does not exists or isn't an directory.");
//
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		this.root = rootDir.toAbsolutePath().toString();
		this.root = root;
		this.options = options;
	}

	@Override
	public void before(Request req, Response res) {
		String path = req.getURI().getPath();

		// Check context
		String context = req.getContext();
		if (path.indexOf(context) == 0) {
			path = path.substring(context.length());
		}

		// If the path is empty try index.html
//		if (path.length() <= 1)
//			path = "index.html";

		Resource resource = new ClassPathResource(root + "" + path);

//		Path reqFile = Paths.get(root + "\\" + path);
		Path reqFile = null;
		try {

			URL url = FileProvider.class.getClassLoader().getResource("/" + root + "" + path);

			reqFile = Paths.get(url.toURI());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		 * If the file wasn't found, it will search in the target-directory for the file
		 * by the raw-name without extension.
		 */
		if (options.isFallBackSearching() && !Files.exists(reqFile) && !Files.isDirectory(reqFile)) {
			String name = reqFile.getFileName().toString();

			try {
				Path parent = reqFile.getParent();

				// Check if reading is allowed
				if (Files.isReadable(parent)) {

					Optional<Path> founded = Files.walk(parent).filter(sub -> getBaseName(sub).equals(name))
							.findFirst();

					if (founded.isPresent())
						reqFile = founded.get();
				}
			} catch (IOException e) {
				this.logger.log(Level.WARNING, "Cannot walk file tree.", e);
			}
		}

		if (Files.exists(reqFile) && Files.isRegularFile(reqFile)) {

			if (reqFile.getFileName().toString().charAt(0) == '.') {
				switch (options.getDotFiles()) {
				case IGNORE:
					res.setStatus(Status._404);
					return ;
				case DENY:
					res.setStatus(Status._403);
					return ;
				}
			}

			if (options.getExtensions() != null) {
				String reqEx = Utils.getExtension(reqFile);

				if (reqEx == null)
					return ;

				for (String ex : options.getExtensions()) {
					if (reqEx.equals(ex)) {
						finish(reqFile, req, res);
						break;
					}
				}

				res.setStatus(Status._403);
			} else {
				finish(reqFile, req, res);
			}
		}
		return ;
	}

	private void finish(Path file, Request req, Response res) {
		if (options.getHandler() != null)
			options.getHandler().handle(req, res);

		try {

			// Apply header
			if (options.isLastModified())
				res.setHeader("Last-Modified", Utils.getGMTDate(new Date(Files.getLastModifiedTime(file).toMillis())));
		} catch (IOException e) {
			res.sendStatus(Status._500);
			this.logger.log(Level.WARNING, "Cannot read LastModifiedTime from file " + file.toString(), e);
			return;
		}

		res.setHeader("Cache-Control", String.valueOf(options.getMaxAge()));
		send(res, file);
	}

	public boolean send(Response res, Path file) {
		if (res.isClosed() || !Files.isRegularFile(file))
			return false;

		try {
			res.contentLength = Files.size(file);

			// Detect content type
			MediaType mediaType = Utils.getContentType(file);
			res.contentType = mediaType == null ? null : mediaType.getMIME();

			// Send header
			res.sendHeaders();

			// Send file
			InputStream fis = Files.newInputStream(file, StandardOpenOption.READ);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = fis.read(buffer)) != -1) {
				res.body.write(buffer, 0, n);
			}

			fis.close();

		} catch (IOException e) {
			logger.log(Level.INFO, "Failed to pipe file to outputstream.", e);
			res.close();
			return false;
		}

		res.close();
		return true;
	}

	/**
	 * Sets the 'Content-Disposition' header to 'attachment' and his
	 * Content-Disposition "filename=" parameter to the file name. Normally this
	 * triggers an download event client-side.
	 *
	 * @param file The file which will be send as attachment.
	 * @return True if the file was successfully send, false if the file doesn't
	 *         exists or the respose is already closed.
	 */
	public boolean sendAttachment(Response res, Path file) {
		if (res.isClosed() || !Files.isRegularFile(file))
			return false;

		String dispo = "attachment; filename=\"" + file.getFileName() + "\"";
		res.setHeader("Content-Disposition", dispo);

		return send(res, file);
	}

	private String getBaseName(Path path) {
		String name = path.getFileName().toString();
		int index = name.lastIndexOf('.');
		return index == -1 ? name : name.substring(0, index);
	}

	/**
	 * Returns the logger which is concered for this FileProvilder object. There is
	 * no default-handler active, if you want to log it you need to set an handler.
	 *
	 * @return The logger from this FileProvilder object.
	 */
	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getName() {
		return "file";
	}

}
