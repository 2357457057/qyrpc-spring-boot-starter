import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Arrays;

public class aaaaa {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(StringUtils.tokenizeToStringArray("top.yqingyu.**.aaa.*", ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS)));
    }
}
