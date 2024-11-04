package cn.afternode.red4j.api;

public class RED4JApi {
    private static RED4J api;

    public static void setApi(RED4J api) {
        if (RED4JApi.api != null)
            throw new UnsupportedOperationException("Cannot overwrite existing API implementation");
        RED4JApi.api = api;
    }

    public static RED4J getApi() {
        return api;
    }
}
