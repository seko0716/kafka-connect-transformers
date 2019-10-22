package seko.kafka.connect.transformer.script.configs;

import java.util.Arrays;
import java.util.List;

public final class Configuration {
    public static final String SCRIP_ENGINE_NAME = "scrip_engine_name";

    public static final String KEY_SCRIPT_CONFIG = "key.script";
    public static final String VALUE_SCRIPT_CONFIG = "value.script";
    public static final String FIELD_FOR_EXCEPTION = "field_for_exception";
    public static final String FIELD_FOR_EXCEPTION_DEFAULT = "script_engine_transformer_tags";

    public static final List<String> JS_SCRIPT_ENGINE_NAMES = Arrays.asList("nashorn", "javascript");

    private Configuration() {
    }
}
