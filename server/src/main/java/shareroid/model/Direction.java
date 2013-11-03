package shareroid.model;

import java.util.Map;

import org.slim3.controller.validator.Validators;

import shareroid.meta.ShareMeta;

public enum Direction {

    CHROME("CHROME"),
    ANDROID("ANDROID");

    private String name;

    private Direction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Direction parse(Map<String, Object> request) {
        Direction direction = null;

        if (request != null) {
            try {
                Validators v = new Validators(request);
                v.add(ShareMeta.get().direction, v.required());

                if (v.validate()) {
                    direction = Direction.valueOf((String)request.get("direction"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return direction;
    }
}