import { LoginRequest } from "../features/auth/interfaces/loginRequest.interface";
import { RegisterRequest } from "../features/auth/interfaces/registerRequest.interface";
import { SessionInformation } from "../interfaces/sessionInformation.interface";




  export const mockLoginRequest: LoginRequest = {
    email: 'johndoe',
    password: 'password123',
  };

  
  export const registerRequest: RegisterRequest = {
    email: 'jdoe@mx.com',
    firstName: 'John',
    lastName: 'Doe',
    password: '123',
  };