package most.qms.services;

import most.qms.models.DailyCounter;
import most.qms.repositories.DailyCounterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DailyCounterService {
    private final DailyCounterRepository repo;

    @Autowired
    public DailyCounterService(DailyCounterRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Long getAndIncrement() {
        DailyCounter counter;
        List<DailyCounter> all = repo.findAll();
        if (!all.isEmpty()) {
            counter = all.getFirst();
        } else {
            counter = repo.save(new DailyCounter());
        }
        Long res = counter.getCounter();
        counter.increment();
        repo.save(counter);
        return res;
    }
}
