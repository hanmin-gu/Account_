package com.example.account.AccountException;

import com.example.account.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class accountException extends RuntimeException{
    private ErrorCode  errorCode;
    private String errorMessage;

    public accountException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }


}
