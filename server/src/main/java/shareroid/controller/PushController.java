package shareroid.controller;

import org.slim3.controller.Navigation;

public class PushController extends ShareController {

    @Override
    protected Navigation run() throws Exception {
        protectOAuthRequest();

        if (isPost()) {
            service.save(asMap());
        } else {
            sendError();
        }

        return null;
    }
}