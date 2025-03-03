import { SessionInformation } from "../interfaces/sessionInformation.interface";
import { Teacher } from "../interfaces/teacher.interface";
import { User } from "../interfaces/user.interface";


export const mockSessionInformation: SessionInformation = {
    token: 'mockToken',
    type: 'bearer',
    id: 1,
    username: 'johndoe',
    firstName: 'John',
    lastName: 'Doe',
    admin: true,
  };

  export const mockSessionService = {
     sessionInformation: mockSessionInformation,
   };

  
 export const mockTeacher: Teacher = {
    id: 1,
    firstName: 'Dalaî',
    lastName: 'Lama',
    createdAt: new Date(),
    updatedAt: new Date(),
  };


  export const mockUser: User = {
    id: 1,
    email: 'jdoe@mx.com',
    lastName: 'Doe',
    firstName: 'John',
    admin: true,
    password: '123',
    createdAt: new Date(),
    updatedAt: new Date()
  }
  

   
      
   