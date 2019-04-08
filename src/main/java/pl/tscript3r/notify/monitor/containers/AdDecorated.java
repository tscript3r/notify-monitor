package pl.tscript3r.notify.monitor.containers;

import pl.tscript3r.notify.monitor.domain.Ad;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

class AdDecorated {

    private Ad ad;
    Boolean returned = false;

    private AdDecorated(Ad ad) {
        this.ad = ad;
    }

    static SizeLimitedLinkedHashSet<AdDecorated> adsToAdDecoratedSizeLimitedSet(Integer sizeLimit, Collection<Ad> ads) {
        return ads.stream()
                .map(AdDecorated::new)
                .collect(Collectors.toCollection(() -> new SizeLimitedLinkedHashSet<>(sizeLimit)));
    }

    static LinkedHashSet<AdDecorated> adsToAdDecoratedSizeLimitedSet(Collection<Ad> ads) {
        return ads.stream()
                .map(AdDecorated::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    static LinkedHashSet<Ad> adsDecoratedToAdsSet(Collection<AdDecorated> ads) {
        return ads.stream()
                .map(adDecorated -> adDecorated.ad)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AdDecorated)) return false;
        AdDecorated that = (AdDecorated) o;
        return Objects.equals(ad, that.ad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ad);
    }

}
