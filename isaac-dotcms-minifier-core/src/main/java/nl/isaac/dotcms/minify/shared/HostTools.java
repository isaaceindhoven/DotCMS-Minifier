package nl.isaac.dotcms.minify.shared;

/*
 * Dotcms minifier by ISAAC is licensed under a
 * Creative Commons Attribution 3.0 Unported License
 *
 * - http://creativecommons.org/licenses/by/3.0/
 *
 * ISAAC Software Solutions B.V. (http://www.isaac.nl)
 */

import javax.servlet.http.HttpServletRequest;

import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.util.PageMode;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;

public class HostTools {
	/**
	 * Retrieve the current host from the request
	 * @return the current host
	 * @throws RuntimeException an exception that wraps the actual dotCMS exception when the host can't be found
	 */
	public static Host getCurrentHost(HttpServletRequest request) {
		try {
			return WebAPILocator.getHostWebAPI().getCurrentHost(request);
		} catch (PortalException e) {
			throw new RuntimeException(e);
		} catch (SystemException e) {
			throw new RuntimeException(e);
		} catch (DotDataException e) {
			throw new RuntimeException(e);
		} catch (DotSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static Host getHostByDomainName(String domainName) {

		try {

			// Find Host by Name
			Host host = APILocator.getHostAPI().findByName(domainName, APILocator.getUserAPI().getSystemUser(), false);

			// If not found, find Host by Alias
			host = host != null? host: APILocator.getHostAPI().findByAlias(domainName, APILocator.getUserAPI().getSystemUser(), false);

			return host;
		} catch (DotDataException e) {
			throw new RuntimeException("Unable to find host for domain name " + domainName, e);
		} catch (DotSecurityException e) {
			throw new RuntimeException("Unable to find host for domain name " + domainName, e);
		}
	}

	public static Host getHostByIdentifier(String identifier) {

		try {

			return  APILocator.getHostAPI().find(identifier, APILocator.getUserAPI().getSystemUser(), false);

		} catch (DotDataException e) {
			throw new RuntimeException("Unable to find host for identifier " + identifier, e);
		} catch (DotSecurityException e) {
			throw new RuntimeException("Unable to find host for identifier  " + identifier, e);
		}
	}


	public static boolean isLiveMode(HttpServletRequest request) {
		boolean EDIT_MODE = PageMode.get(request) == PageMode.EDIT_MODE;
		boolean PREVIEW_MODE = PageMode.get(request) == PageMode.PREVIEW_MODE;

		return !(EDIT_MODE || PREVIEW_MODE);
	}
}
