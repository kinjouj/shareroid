package shareroid.controller;

import java.util.List;

import org.slim3.controller.Navigation;

import shareroid.model.Direction;
import shareroid.model.Share;

public class ReadController extends ShareController {
    @Override
    protected Navigation run() throws Exception {
        protectOAuthRequest();

        if (isGet()) {
            List<Share> shares = service.getSharesByDirection(
                Direction.parse(asMap())
            );
            String json = service.modelsToJson(shares);
            renderJSON(json);
        } else {
            sendError();
        }

        return null;
    }
}