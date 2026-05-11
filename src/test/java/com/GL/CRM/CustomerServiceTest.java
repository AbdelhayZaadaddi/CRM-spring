package com.GL.CRM;

import com.GL.CRM.customer.dto.CustomerRequest;
import com.GL.CRM.customer.dto.CustomerResponse;
import com.GL.CRM.customer.entity.Customer;
import com.GL.CRM.customer.mapper.CustomerMapper;
import com.GL.CRM.customer.repository.CustomerRepository;
import com.GL.CRM.customer.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository ;  // mock repo

    @Mock
    private CustomerMapper customerMapper ;      //mock mapper

    @InjectMocks
    private CustomerService customerService;


     private Customer customer;
    private  CustomerRequest request ;
    private CustomerResponse response ;


    @BeforeEach
    void setUp() {
        // build sample customer obj
        customer = Customer.builder( )
                 .id(1L)
                .name("Ilyasse Younes")
                .email("ilyasse@gmail.com")
                .phone("0612345678")
                 .company("Cadi Ayyad")
                .notes("test customer")
                .createdAt(LocalDateTime.now() )
                .updatedAt(LocalDateTime.now())
                .build() ;

        // request dto
        request = new CustomerRequest ();
        request.setName("Ilyasse Younes");
        request.setEmail("ilyasse@gmail.com");
        request.setPhone("0612345678");
        request.setCompany("Cadi Ayyad");
         request.setNotes("test customer");

        // response dtoo
        response = CustomerResponse.builder( )
                .id(1L)
                 .name("Ilyasse Younes")
                 .email("ilyasse@gmail.com")
                 .phone("0612345678")
                 .company("Cadi Ayyad")
                 .notes("test customer" )
                .build( );
    }


    @Test
    void getAll_shouldReturnListOfCustomers() {
        when (customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(response) ;

        List<CustomerResponse> result = customerService.getAll();

         assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ilyasse Younes") ;

    }


    @Test
    void getById_shouldReturnCustomer_whenFound() {
        when(customerRepository.findById(1L)) .thenReturn(Optional.of(customer)) ;

         when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.getById(1L) ;

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("ilyasse@gmail.com") ;
    }


    @Test
    void getById_shouldThrow_whenNotFound() {
        when(customerRepository. findById(99L)) .thenReturn(Optional.empty()) ;

        assertThrows(EntityNotFoundException.class, ( ) -> customerService.getById(99L)) ;

    }


    @Test
    void create_shouldSaveAndReturnCustomer() {
        when(customerRepository.existsByEmail("ilyasse@gmail.com")).thenReturn(false) ;
        when(customerMapper.toEntity(request)).thenReturn(customer);
         when(customerRepository.save(customer)).thenReturn(customer  );
        when(customerMapper.toResponse(customer)).thenReturn(response);

        CustomerResponse result = customerService.create(request);

        assertThat(result.getName()).isEqualTo("Ilyasse Younes");
        verify(customerMapper).toEntity(request);
         verify(customerRepository).save(customer) ;
        verify(customerMapper).toResponse(customer );
    }


    @Test
    void create_shouldThrow_whenEmailAlreadyUsed() {
         when(customerRepository.existsByEmail("ilyasse@gmail.com")) .thenReturn (true) ;

        assertThrows(RuntimeException.class, () -> customerService.create(request));
        verify (customerRepository, never()).save(any()) ;
    }


    @Test
    void update_shouldUpdateAndReturnCustomer( ) {
        when(customerRepository.findById(1L)).thenReturn(Optional.of( customer));
        when (customerRepository.save(customer)).thenReturn(customer);
         when( customerMapper.toResponse(customer)).thenReturn(response );

        CustomerResponse result = customerService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Ilyasse Younes");
        verify (customerMapper).updateEntity(customer, request);
        verify(customerRepository) .save(customer);
    }


    @Test
    void update_shouldThrow_whenCustomerNotFound() {
        when( customerRepository. findById(99L)).thenReturn(Optional.empty());

         assertThrows(EntityNotFoundException.class, () -> customerService. update(99L, request));
    }


    @Test
    void delete_shouldDeleteCustomer_whenExists() {
        when(customerRepository.
                existsById(1L)).thenReturn(true);

        customerService.delete(1L);

        verify (customerRepository).deleteById(1L); }


    @Test
    void delete_shouldThrow_whenCustomerNotFound( ) {
        when(customerRepository.existsById(99L)) .thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> customerService.delete(99L));
         verify (customerRepository, never()).deleteById(any());}
}
