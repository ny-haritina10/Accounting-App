package mg.module.accounting.utils;

import org.springframework.stereotype.Component;

@Component
public class ReferenceCodeUtil {
    
    public static String getReferenceCode(String id, String prefix) {
        return id + "-" + prefix;
    }
}