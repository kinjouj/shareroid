package shareroid.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;

import shareroid.service.ShareService;

public abstract class ShareController extends Controller {

    protected ShareService service = new ShareService();

    protected void protectOAuthRequest() throws IOException, OAuthRequestException {
        OAuthService oauthService = service.getOAuthService();

        if (!oauthService.isUserAdmin()) {
            throw new OAuthRequestException("unauthorized");
        }
    }

    protected void sendError() throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    protected void renderJSON(String json) throws IOException {
        if (StringUtil.isEmpty(json)) {
            throw new IllegalArgumentException("json is empty");
        }

        response.setStatus(200);
        response.setContentType("application/json; charset=utf-8");
        response.setContentLength(json.length());
        response.getWriter().print(json);
    }

    @Override
    protected Navigation handleError(Throwable error) throws Throwable {
        response.sendError(
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            error.getMessage()
        );

        return null;
    }
}
