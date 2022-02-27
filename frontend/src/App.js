import { BrowserRouter, Routes, Route } from "react-router-dom";
import HomePage from "main/pages/HomePage";
import ProfilePage from "main/pages/ProfilePage";
import AdminUsersPage from "main/pages/AdminUsersPage";

import TodosIndexPage from "main/pages/Todos/TodosIndexPage";
import TodosCreatePage from "main/pages/Todos/TodosCreatePage";
import TodosEditPage from "main/pages/Todos/TodosEditPage";

import UCSBDatesIndexPage from "main/pages/UCSBDates/UCSBDatesIndexPage";
import UCSBDatesCreatePage from "main/pages/UCSBDates/UCSBDatesCreatePage";
import UCSBDatesEditPage from "main/pages/UCSBDates/UCSBDatesEditPage";

import StudentsIndexPage from "main/pages/Students/StudentsIndexPage";
import StudentsCreatePage from "main/pages/Students/StudentsCreatePage";


import UCSBSubjectsIndexPage from "main/pages/UCSBSubjects/UCSBSubjectsIndexPage";
import UCSBSubjectsCreatePage from "main/pages/UCSBSubjects/UCSBSubjectsCreatePage";
import UCSBSubjectsEditPage from "main/pages/UCSBSubjects/UCSBSubjectsEditPage";
import CollegeSubredditsIndexPage from "main/pages/CollegeSubreddits/CollegeSubredditsIndexPage";
import CollegeSubredditsCreatePage from "main/pages/CollegeSubreddits/CollegeSubredditsCreatePage";
import CollegeSubredditsEditPage from "main/pages/CollegeSubreddits/CollegeSubredditsEditPage";
import EarthquakesIndexPage from "main/pages/Earthquakes/EarthquakesIndexPage.sample.";
import EarthquakesCreatePage from "main/pages/Earthquakes/EarthquakesCreatePage";
import { hasRole, useCurrentUser } from "main/utils/currentUser";

import "bootstrap/dist/css/bootstrap.css";


function App() {

  const { data: currentUser } = useCurrentUser();

  return (
    <BrowserRouter>
      <Routes>
        <Route exact path="/" element={<HomePage />} />
        <Route exact path="/profile" element={<ProfilePage />} />
        {
          hasRole(currentUser, "ROLE_ADMIN") && <Route exact path="/admin/users" element={<AdminUsersPage />} />
        }
        {
          hasRole(currentUser, "ROLE_USER") && (
            <>
              <Route exact path="/todos/list" element={<TodosIndexPage />} />
              <Route exact path="/todos/create" element={<TodosCreatePage />} />
              <Route exact path="/todos/edit/:todoId" element={<TodosEditPage />} />
            </>
          )
        }


        {
          hasRole(currentUser, "ROLE_USER") && (
            <>
              <Route exact path="/students/list" element={<StudentsIndexPage />} />
            </>
          )
        }
        {
          hasRole(currentUser, "ROLE_ADMIN") && (
            <>
              <Route exact path="/students/create" element={<StudentsCreatePage />} />
            </>
          )
        }

        {
          hasRole(currentUser, "ROLE_USER") && (
            <>
              <Route exact path="/ucsbdates/list" element={<UCSBDatesIndexPage />} />
            </>
          )
        }
        {
          hasRole(currentUser, "ROLE_ADMIN") && (
            <>
              <Route exact path="/ucsbdates/edit/:id" element={<UCSBDatesEditPage />} />
              <Route exact path="/ucsbdates/create" element={<UCSBDatesCreatePage />} />
            </>
          )
        }

        {
          hasRole(currentUser, "ROLE_USER") && (
            <>

              <Route exact path="/ucsbsubjects/list" element={<UCSBSubjectsIndexPage />} />

              <Route exact path="/collegesubreddits/list" element={<CollegeSubredditsIndexPage />} />

              <Route exact path="/earthquakes/list" element={<EarthquakesIndexPage />} />

            </>
          )
        }
        {
          hasRole(currentUser, "ROLE_ADMIN") && (
            <>
              <Route exact path="/collegesubreddits/edit/:id" element={<CollegeSubredditsEditPage />} />
              <Route exact path="/collegesubreddits/create" element={<CollegeSubredditsCreatePage />} />
                
              <Route exact path="/ucsbsubjects/edit/:id" element={<UCSBSubjectsEditPage />} />
              <Route exact path="/ucsbsubjects/create" element={<UCSBSubjectsCreatePage />} />

              <Route exact path="/earthquakes/retrieve" element={<EarthquakesCreatePage />} />

            </>
          )
        }

      </Routes>
    </BrowserRouter>
  );
}

export default App;
