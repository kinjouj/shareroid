package shareroid.controller;

import org.slim3.controller.Navigation;

public class HistoryController extends ShareController {
    @Override
    protected Navigation run() throws Exception {
        protectOAuthRequest();

        if (isGet()) {
            String json = service.modelsToJson(service.getHistory());
            renderJSON(json);
        } else {
            sendError();
        }

        return null;
    }
}
