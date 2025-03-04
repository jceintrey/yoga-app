export const admin = {
  id: 1,
  username: 'userName',
  firstName: 'firstName',
  lastName: 'lastName',
  admin: true,
  email: 'yoga@studio.com',
  password: 'test!1234',
};
export const jdoe = {
  id: 2,
  username: 'jdoe@mx.com',
  firstName: 'John',
  lastName: 'Doe',
  email: 'jdoe@mx.com',
  password: 'test!1234',
  admin: false,
  createdAt: new Date().toISOString().split('T')[0],
  updatedAt: new Date().toISOString().split('T')[0],
};

export const sessions = ['session1', 'session2', 'session3'].map(
  (name, index) => ({
    id: index + 1,
    name: `Yoga ${name}`,
    description: `A simple Yoga ${name}`,
    date: new Date(),
    teacher_id: 1,
    users: [1, 2],
    createdAt: new Date().toISOString().split('T')[0],
    updatedAt: new Date().toISOString().split('T')[0],
  })
);

export const anothersamplesession = {
  id: 999,
  name: 'Another Sample Session',
  description: 'the description ...',
  date: new Date().toISOString().split('T')[0],
  teacher_id: 2,
};

export const teachers = [
  { firstName: 'Bruce', lastName: 'Lee' },
  { firstName: 'Kathryn', lastName: 'Budig' },
  { firstName: 'Adriene', lastName: 'Mischler' },
].map((teacher, index) => ({
  id: index + 1,
  firstName: teacher.firstName,
  lastName: teacher.lastName,
  createdAt: new Date().toISOString().split('T')[0],
  updatedAt: new Date().toISOString().split('T')[0],
}));
