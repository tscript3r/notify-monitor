package pl.tscript3r.notify.monitor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED, reason = "Required hostname not handled")
public class IncompatibleHostnameException extends RuntimeException {

    // TODO: implement HostnameValidator

    public IncompatibleHostnameException(String hostname) {
        super("Incompatible hostname=" + hostname);
    }

}
