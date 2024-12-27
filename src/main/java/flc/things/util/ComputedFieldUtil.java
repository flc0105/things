package flc.things.util;

import flc.things.entity.ItemCustomFieldValue;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ComputedFieldUtil {

    private final ScriptEngineManager manager = new ScriptEngineManager();
    private final ScriptEngine engine = manager.getEngineByName("JavaScript");

    public Object evaluateExpression(String expression, Map<String, Object> context) throws ScriptException {
        // 将上下文变量添加到脚本引擎中
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            engine.put(entry.getKey(), entry.getValue());
        }
        // 执行表达式
        return engine.eval(expression);
    }

}
