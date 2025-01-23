package flc.things.util;

import flc.things.annotation.Translator;
import flc.things.service.DictDataService;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TranslatorUtil {

    private static final Map<String, String> dictCache = new ConcurrentHashMap<>();

    private static DictDataService dictDataService;

    public TranslatorUtil(DictDataService dictDataService) {
        TranslatorUtil.dictDataService = dictDataService;
    }


    private static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        while (type != null) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            type = type.getSuperclass();
        }
        return fields;
    }

    public static <T> void translate(T item) {
        List<Field> fields = getAllFields(item.getClass());
        List<Field> formatterFields = fields.stream()
                .filter(field -> field.isAnnotationPresent(Translator.class))
                .collect(Collectors.toList());

        formatterFields.forEach(field -> format(item, field));
    }

    private static <T> void format(T item, Field field) {
        try {
            field.setAccessible(true);
            Translator translator = field.getAnnotation(Translator.class);
            String code = (String) field.get(item);
            if (code != null && !code.isEmpty()) {
                String dictName = getDictName(translator.dictCode(), code);
                Field strField = item.getClass().getDeclaredField(field.getName() + "Str");
                strField.setAccessible(true);
                strField.set(item, dictName);
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            System.err.println("Translate error: " + e.getMessage());
        }
    }

    private static String getDictName(String dictType, String code) {
        return dictCache.computeIfAbsent(code, c -> dictDataService.getDictDataByDictCodeAndCode(dictType, c).getName());
    }
}






