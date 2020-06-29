package org.cheeryworks.liteql.model.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class StringUtil {

    private StringUtil() {

    }

    public static Set<String> convertDelimitedParameterToSetOfString(String ids) {
        Set<String> selectedIds = new HashSet<String>();
        if (!StringUtils.isEmpty(ids)) {
            String[] idsInArray = ids.split("[,]");
            for (String id : idsInArray) {
                selectedIds.add(id);
            }
        }

        return selectedIds;
    }

    public static Set<Long> convertDelimitedParameterToSetOfLong(String ids) {
        Set<Long> selectedIds = new HashSet<>();
        if (!StringUtils.isEmpty(ids)) {
            String[] idsInArray = ids.split("[,]");
            for (String id : idsInArray) {
                selectedIds.add(Long.parseLong(id));
            }
        }

        return selectedIds;
    }

    public static String httpUrlConcat(String baseUrl, String path) {
        if (baseUrl == null || path == null) {
            throw new IllegalArgumentException("baseUrl and path must not be null");
        }

        if (!baseUrl.trim().endsWith("/")) {
            baseUrl = baseUrl.trim() + "/";
        }

        if (path.trim().startsWith("/")) {
            path = path.trim().substring(1);
        }

        return baseUrl + path;
    }

    public static String camelNameToLowerDashConnectedLowercaseName(String camelName) {
        String[] words = StringUtils.splitByCharacterTypeCamelCase(camelName);

        return String.join("_", words).toLowerCase();
    }

    public static String lowerDashConnectedLowercaseNameToUncapitalizedCamelName(
            String lowerDashConnectedLowercaseName) {
        String[] words = lowerDashConnectedLowercaseName.split("_");

        List<String> capitalizedWords = new ArrayList<>();

        for (String word : words) {
            capitalizedWords.add(StringUtils.capitalize(word));
        }

        return StringUtils.uncapitalize(
                String.join("", capitalizedWords.toArray(new String[capitalizedWords.size()])));
    }

    public static String plural(String word) {
        return English.plural(word);
    }

}
