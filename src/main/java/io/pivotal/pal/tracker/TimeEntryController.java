package io.pivotal.pal.tracker;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.Counter;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;
    private TimeEntryRepository timeEntryRepository;

    public TimeEntryController(
            TimeEntryRepository timeEntriesRepo,
            MeterRegistry meterRegistry
    ) {
        this.timeEntryRepository = timeEntriesRepo;

        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }




    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntry);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());
        return new ResponseEntity<TimeEntry>(createdTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TimeEntry> read(@PathVariable("id") Long id) {
        TimeEntry readTimeEntry = timeEntryRepository.find(id);
        if (readTimeEntry != null) {
            actionCounter.increment();
            return new ResponseEntity<>(readTimeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping(value = "/{id}", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TimeEntry> update(@PathVariable("id") Long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry updatedTimeEntry = timeEntryRepository.update(id, timeEntry);
        if (updatedTimeEntry != null) {
            actionCounter.increment();
            return new ResponseEntity<>(updatedTimeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") Long id) {
        timeEntryRepository.delete(id);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TimeEntry>> list() {
        actionCounter.increment();
        return new ResponseEntity<List<TimeEntry>>(timeEntryRepository.list(), HttpStatus.OK);
    }

    private ResponseEntity<TimeEntry> generateTimeEntryResponse(TimeEntry timeEntry) {
        return new ResponseEntity<TimeEntry>(timeEntry, timeEntry == null ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

}
