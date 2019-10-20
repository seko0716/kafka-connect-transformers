package seko.kafka.connect.transformer.script.configs;

public final class Configuration {
    public static final String SCRIP_ENGINE_NAME = "scrip_engine_name";

    public static final String KEY_SCRIPT_CONFIG = "key.script";
    public static final String VALUE_SCRIPT_CONFIG = "value.script";
    public static final String FIELD_FOR_EXCEPTION = "field_for_exception";
    public static final String FIELD_FOR_EXCEPTION_DEFAULT = "script_engine_transformer_tags";

    private Configuration() {
    }
}
