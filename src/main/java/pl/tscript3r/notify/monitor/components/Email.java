package pl.tscript3r.notify.monitor.components;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pl.tscript3r.notify.monitor.api.v1.model.AdDTO;
import pl.tscript3r.notify.monitor.containers.SizeLimitedLinkedHashSet;

import java.util.Collection;

@Slf4j
@Getter
public class Email {

    private final String receiver;
    private final SizeLimitedLinkedHashSet<AdDTO> content;

    public Email(String receiver, SizeLimitedLinkedHashSet<AdDTO> content) {
        this.receiver = receiver;
        this.content = content;
    }

    public static Builder build() {
        return new Builder();
    }

    public Boolean canAdd(String receiver) {
        return this.receiver.equals(receiver) && !content.isFull();
    }

    public void addAds(Collection<AdDTO> ads) {
        content.addUntilLimitReached(ads);
    }

    public static final class Builder {

        private String receiver;
        private SizeLimitedLinkedHashSet<AdDTO> content;

        public Builder receiver(String receiver) {
            this.receiver = receiver;
            return this;
        }

        public Builder content(Collection<AdDTO> ads, int adsPerEmail) {
            this.content = new SizeLimitedLinkedHashSet<>(adsPerEmail);
            this.content.addUntilLimitReached(ads);
            return this;
        }

        public Email get() {
            log.debug("Created new email for receiver={}", this.receiver);
            return new Email(receiver, content);
        }

    }

}
