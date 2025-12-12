package eg.alrawi.alrawi_award.mail;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Email {

    private String from;
    private String fromEmail;
    private String to;
    private String subject;
    private String templateName;
    private Map<String,String> templateTokens = new HashMap<>();




}
