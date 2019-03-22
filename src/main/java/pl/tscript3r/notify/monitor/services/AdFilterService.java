package pl.tscript3r.notify.monitor.services;

import pl.tscript3r.notify.monitor.domain.Ad;
import pl.tscript3r.notify.monitor.domain.Task;
import pl.tscript3r.notify.monitor.filters.AdFilter;

import java.util.List;

public interface AdFilterService {

    void add(Task task, AdFilter adFilter);

    void remove(Task task);

    List<Ad> filter(List<Ad> ads);

    /*
{
    "url": "https://www.olx.pl/moda/?view=list",
    "users_id": [
    	1
    ],
    "filters": [
        {
        	"filter_type": "match",
        	"property": "title",
        	"case_sensitive": false,
        	"words": [
        		"A",
        		"B"
        	]
        }
    ]
}
     */

}
