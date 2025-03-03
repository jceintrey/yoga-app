import { Session } from "../features/sessions/interfaces/session.interface";



  export const mockSession: Session = {
    id: 1,
    name: 'Yoga session',
    description: 'A yoga session',
    date: new Date(),
    teacher_id: 1,
    users: [1, 2],
    createdAt: new Date(),
    updatedAt: new Date(),
  };
  
  export const mockSession2: Session = {
    id: 2,
    name: 'Another Yoga session',
    description: 'Another yoga session',
    date: new Date(),
    teacher_id: 1,
    users: [2],
    createdAt: new Date(),
    updatedAt: new Date(),
  };

  export const mockSessions: Session[] = [
    mockSession,
    mockSession2
  ]