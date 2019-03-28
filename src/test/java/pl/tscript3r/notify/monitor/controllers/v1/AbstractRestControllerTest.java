package pl.tscript3r.notify.monitor.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;

class AbstractRestControllerTest {

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
