package tech.muyi.util.freemark;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.utility.StringUtil;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: muyi
 * @date: 2021-01-12 22:42
 */
public class FreeMarkerUtil {
    public static Configuration configuration;

    static {
        configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setObjectWrapper(Configuration.getDefaultObjectWrapper(Configuration.VERSION_2_3_23));
        configuration.setDefaultEncoding("utf-8");
        configuration.setClassForTemplateLoading(StringUtil.class, "/");
    }

    public static String getProcessValue(Map<String, String> param, String temp) {
        try {
            Template template = new Template("", new StringReader("<#escape x as (x)!>" + temp + "</#escape>"),
                    configuration);
            StringWriter sw = new StringWriter();
            template.process(param, sw);
            return sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("a","A");
        hashMap.put("b","B");
        String temp = "${a}111111+${b}";

        System.out.println(getProcessValue(hashMap,temp));
    }
}
