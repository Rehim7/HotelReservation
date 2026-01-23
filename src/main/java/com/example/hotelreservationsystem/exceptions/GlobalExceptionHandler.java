package com.example.hotelreservationsystem.exceptions;

import com.example.hotelreservationsystem.dto.request.HotelRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import com.example.hotelreservationsystem.dto.response.ErrorResponse;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(RoomAlreadyExist.class)
    public ResponseEntity<String> handleRoomAlreadyExist(RoomAlreadyExist ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


   @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginExceptionHandler(LoginException e) {
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
   }

   @ExceptionHandler(HotelNotFoundException.class)
   public ResponseEntity<String> hotelNotFoundExceptionHandler(HotelNotFoundException e) {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

   }

   @ExceptionHandler(SomethingWentWrong.class)
   public ResponseEntity<String> somethingWentWrongExceptionHandler(SomethingWentWrong e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
   }
   @ExceptionHandler(HotelAlreadyExist.class)
   public ResponseEntity<String> hotelAlreadyExist(HotelAlreadyExist e ) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
   }

   @ExceptionHandler(TicketDoesntExist.class)
   public ResponseEntity<String> ticketDoesntExist(TicketDoesntExist e ) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
   }
   @ExceptionHandler(CardNotFound.class)
   public ResponseEntity<String> cardNotFoundExceptionHandler(CardNotFound e) {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
   }

   @ExceptionHandler(CardAlreadyExist.class)
   public ResponseEntity<String> cardAlreadyExistExceptionHandler(CardAlreadyExist e) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
   }

   @ExceptionHandler(BalanceIsNotEnough.class)
   public ResponseEntity<String> balanceIsNotEnoughExceptionHandler(BalanceIsNotEnough e) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
   }

   @ExceptionHandler(RegisterExceptions.class)
    public  ResponseEntity<String> registerExceptionHandler(RegisterExceptions e) {
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
   }

   @ExceptionHandler(RoomReservedException.class)
    public  ResponseEntity<String> roomReservedExceptionHandler(RoomReservedException e) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
   }


   @ExceptionHandler(TokenRefreshException.class)
   public  ResponseEntity<String> tokenRefreshExceptionHandler(TokenRefreshException e) {
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

   }

   @ExceptionHandler(RoomNotFound.class)
    public  ResponseEntity<String> roomNotFoundExceptionHandler(RoomNotFound e) {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<String> errorMessages = result.getFieldErrors().stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation failed", errorMessages);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
   }

   @ExceptionHandler(AuthenticationException.class)
   public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
       return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
   }

   @ExceptionHandler(AccessDeniedException.class)
   public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: You don't have permission to access this resource");
   }
}

