package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {

    TimeEntry create(TimeEntry timeEntry);
    TimeEntry update(Long id, TimeEntry expectedTimeEntry);
    void delete(Long id);
    List<TimeEntry> list();
    TimeEntry find(Long id);
}
