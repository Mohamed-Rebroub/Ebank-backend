package com.sid.ebankbackend;

import com.sid.ebankbackend.dtos.BankAccountDTO;
import com.sid.ebankbackend.dtos.CurrentBankAccountDTO;
import com.sid.ebankbackend.dtos.CustomerDTO;
import com.sid.ebankbackend.dtos.SavingBankAccountDTO;
import com.sid.ebankbackend.entities.AccountOperation;
import com.sid.ebankbackend.entities.CurrentAccount;
import com.sid.ebankbackend.entities.Customer;
import com.sid.ebankbackend.entities.SavingAccount;
import com.sid.ebankbackend.enums.AccountStatus;
import com.sid.ebankbackend.enums.OperationType;
import com.sid.ebankbackend.exceptions.BalanceNotSufficientException;
import com.sid.ebankbackend.exceptions.BankAccountNotFoundException;
import com.sid.ebankbackend.exceptions.CustomerNotFoundException;
import com.sid.ebankbackend.repositories.AccountOperationRepository;
import com.sid.ebankbackend.repositories.BankAccountRepository;
import com.sid.ebankbackend.repositories.CustomerRepository;
import com.sid.ebankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankBackendApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService){

		return args -> {
			Stream.of("Hassan","Imane","Amine").forEach(name->{
				CustomerDTO customer=new CustomerDTO();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});
			bankAccountService.listCustomer().forEach(customer->{
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());
					List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
					for (BankAccountDTO bankAccount:bankAccounts) {

						for (int i = 0; i < 10; i++) {
							String accountId;
							if (bankAccount instanceof SavingBankAccountDTO){
								accountId=((SavingBankAccountDTO)bankAccount).getId();
							}else {
								accountId=((CurrentBankAccountDTO) bankAccount).getId();
							}
							bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
							bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");

						}
					}

				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				} catch (BankAccountNotFoundException | BalanceNotSufficientException e) {
					e.printStackTrace();
				}
			});

		};
	}
	//@Bean
	CommandLineRunner Start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository){
		return args -> {
			Stream.of("Amine","Ziko","Sara").forEach(name->{
				Customer customer=new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});
			customerRepository.findAll().forEach(cust->{
				CurrentAccount currentAccount=new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreateAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);


				SavingAccount savingAccount=new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*90000);
				savingAccount.setCreateAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInteresRate(5.5);
				bankAccountRepository.save(savingAccount);
			});
			bankAccountRepository.findAll().forEach(acc->{
				for (int i = 0;i <10; i++) {
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}


			});

		};

	}
}
