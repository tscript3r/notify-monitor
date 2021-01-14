package pl.tscript3r.notify.monitor.consts.v1;

public class Paths {
    private static final String BASE_PATH = "/api/v1";
    public static final String TASK_PATH = BASE_PATH + "/tasks";
    public static final String AD_TASK_PATH = TASK_PATH + "/{id}/ads";
    public static final String STATUS_PATH = BASE_PATH + "/status";
    public static final String USER_PATH = BASE_PATH + "/users";

    private Paths() {
    }

}
