/**
 * Crawler classes in this package are automatically indexed by CrawlerFactory, but there are some requirements:
 * - name pattern: *Crawler,
 * - implement Crawler interface
 * - be a Spring @Component with @Scope "prototype" (every CrawlerThread gets his own instance)
 * - be package-private
 * - equals & hashCode only with his handled hostname
 */
package pl.tscript3r.notify.monitor.crawlers;