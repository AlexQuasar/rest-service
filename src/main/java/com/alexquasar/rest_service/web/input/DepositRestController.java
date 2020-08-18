package com.alexquasar.rest_service.web.input;

import com.alexquasar.rest_service.dto.DepositDTO;
import com.alexquasar.rest_service.dto.DepositFilter;
import com.alexquasar.rest_service.entity.Deposit;
import com.alexquasar.rest_service.service.DepositService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/deposit")
public class DepositRestController {

    private DepositService depositService;

    public DepositRestController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PutMapping("/addDeposit")
    public String addDeposit(@RequestBody DepositDTO deposit) {
        if (depositService.addDeposit(deposit)) {
            return "deposit saved";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @PutMapping("/changeDeposit")
    public String changeDeposit(@RequestParam Long id, @RequestBody DepositDTO deposit) {
        if (depositService.changeDeposit(id, deposit)) {
            return "deposit changed";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @DeleteMapping("/deleteDeposit")
    public String deleteDeposit(@RequestParam Long id) {
        if (depositService.deleteDeposit(id)) {
            return "deposit deleted";
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @GetMapping("/getFilteredAndSortDeposit")
    public Set<DepositDTO> getFilteredAndSortDeposit(@RequestBody DepositFilter depositFilter) {
        return depositService.getFilteredAndSortDeposit(depositFilter);
    }
}
