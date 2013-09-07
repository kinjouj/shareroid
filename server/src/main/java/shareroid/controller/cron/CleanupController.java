package shareroid.controller.cron;

import org.slim3.controller.Navigation;

import shareroid.controller.ShareController;

public class CleanupController extends ShareController {
    @Override
    protected Navigation run() throws Exception {
        if (isGet()) {
            service.cleanup();
        } else {
            sendError();
        }

        return null;
    }
}