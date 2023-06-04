package com.example.account.service;

import com.example.account.AccountException.accountException;
import com.example.account.domain.Account;
import com.example.account.domain.AccountUser;
import com.example.account.dto.AccountDto;
import com.example.account.repository.AccountRepository;
import com.example.account.repository.AccountUserRepository;
import com.example.account.type.AccountStatus;
import com.example.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        AccountUser accountUser =
                accountUserRepository.findById(userId).orElseThrow(() -> new accountException(ErrorCode.USER_NOT_FOUND));

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("1000000000");


        return AccountDto.fromEntity(
                accountRepository.save(Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()));
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository
                .findById(userId).orElseThrow(() -> new accountException(ErrorCode.USER_NOT_FOUND));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new accountException(ErrorCode.USER_NOT_FOUND));
        validateDeleteAccount(accountUser, account);

        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());
        return AccountDto.fromEntity(account);

    }
    private void validateDeleteAccount(AccountUser accountUser, Account account){
        if(accountUser.getId() != account.getAccountUser().getId()) {
            throw new accountException(ErrorCode.USER_ACCOUNT_UNMATCH);
        }

        if(account.getAccountStatus() == AccountStatus.UNREGISTERED){
            throw new accountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }
        if(account.getBalance() > 0){
            throw new accountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }
}
