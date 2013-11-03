package shareroid.controller;

import org.slim3.controller.router.RouterImpl;

public class AppRouter extends RouterImpl {
    public AppRouter() {
        addRouting("/push/chrome", "/push?direction=CHROME");
        addRouting("/read/chrome", "/read?direction=CHROME");
        addRouting("/push/android", "/push?direction=ANDROID");
        addRouting("/read/android", "/read?direction=ANDROID");
    }
}