package nl.isaac.dotcms.minify.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;

import javax.servlet.http.HttpServlet;

import com.dotcms.repackage.org.apache.commons.io.IOUtils;
import com.dotmarketing.beans.Host;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.fileassets.business.FileAsset;
import com.dotmarketing.util.Logger;

import nl.isaac.dotcms.minify.exception.DotCMSFileNotFoundException;
import nl.isaac.dotcms.minify.shared.FileTools;
import nl.isaac.dotcms.minify.shared.HostTools;

public class AbstractMinifyServlet extends HttpServlet {
	protected static final long serialVersionUID = 1L;

	protected static final long BROWSER_CACHE_MAX_AGE = 25920000L;

	protected enum ContentType {
		JS("js", "application/javascript;charset=UTF-8")
		,CSS("css", "text/css;charset=UTF-8");

		private String extension;
		private String contentTypeString;

		ContentType(String extension, String contentTypeString) {
			this.extension = extension;
			this.contentTypeString = contentTypeString;
		}

		public String getContentTypeString() {
			return contentTypeString;
		}

		public String getExtension() {
			return extension;
		}

		static ContentType getContentType(URI uri) {
			for (ContentType ct: ContentType.values()) {
				if (uri.getPath().endsWith(ct.extension)) {
					return ct;
				}
			}
			throw new RuntimeException("Cannot determine contentType from URI " + uri);
		}

	}

	protected Host getHostOfUri(URI uri, Host defaultHost) {

		// If the URI is absolute we determine the host on its domain. (We don't
		// use URI's isAbsolute method because it not understand protocol
		// relative URLs.)
		if (uri.getHost() != null) {
			return HostTools.getHostByDomainName(uri.getHost());
		}

		return defaultHost;
	}

	protected String getFileContent(URI uri, Host host, boolean isLiveMode) throws DotCMSFileNotFoundException {
		FileAsset file = FileTools.getFileAssetByURI(uri.getPath().toString(), host, isLiveMode);

		try {
			if (file != null && file.getURI() != null) {
				StringWriter stringWriter = new StringWriter();
				InputStream input = file.getFileInputStream();
				try {
					IOUtils.copy(input, stringWriter);
				} finally {
					input.close();
				}
				return stringWriter.toString();
			}
		} catch (FileNotFoundException e) {
			Logger.error(MinifyServlet.class, "Could not find file", e);
		} catch (IOException e) {
			Logger.error(MinifyServlet.class, "Could not find file", e);
		} catch (DotDataException e) {
			Logger.error(MinifyServlet.class, "Could not find file", e);
		}
		throw new DotCMSFileNotFoundException("Could not find " + (isLiveMode? "live": "working") + " file '" + uri.toString() + "' on host '" + host.getHostname() + "'.");
	}

}