package pl.tscript3r.notify.monitor.utils;

public class HostnameExtractor {

    public static synchronized String getDomain(String url) {
        String domainName = url;
        int index = domainName.indexOf("://");
        if (index != -1)
            domainName = domainName.substring(index + 3);
        index = domainName.indexOf('/');
        if (index != -1)
            domainName = domainName.substring(0, index);
        domainName = domainName.replaceFirst("^www.*?\\.", "");
        return domainName;
    }

    private HostnameExtractor() {
    }

}
