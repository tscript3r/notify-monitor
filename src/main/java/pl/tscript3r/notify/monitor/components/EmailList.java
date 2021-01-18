package pl.tscript3r.notify.monitor.components;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class EmailList {

    @Getter
    private final List<Email> emails = new ArrayList<>();
    private final int adsPerEmailLimit;

    public void add(String receiver, Collection<AdDTO> ads) {
        boolean splitLogged = false;
        emails.stream()
                .filter(e -> e.canAdd(receiver))
                .forEach(email -> email.addAds(ads));
        while (!ads.isEmpty()) {
            if (!splitLogged) {
                log.debug("Splitting into multiple emails (content size reached) for receiver={}", receiver);
                splitLogged = true;
            }
            emails.add(newEmail(receiver, ads));
        }
    }

    private Email newEmail(String receiver, Collection<AdDTO> ads) {
        return Email.build()
                .receiver(receiver)
                .content(ads, adsPerEmailLimit)
                .get();
    }

}
