package com.library.manG.System.com.library.system.Service;

import com.library.manG.System.com.library.system.utils.BankHeader;
import com.library.manG.System.com.library.system.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import static com.library.manG.System.com.library.system.Application.logger;

@Service
public class serviceCalling {

    private static final Logger log = LoggerFactory.getLogger(serviceCalling.class);

    public String call3PartyApi() throws Exception {
        String response = NetworkUtils.callPostBypassMethod("https://httpbin.org/post", "POST", "{\"key\":\"value\"}", BankHeader.getHeader());
        log.info("Response:--------------------" + response);
        return response;

    }

    public String empty(){
        return "";
    }

    public String getName(){
        return "lakshay";
    }
}
