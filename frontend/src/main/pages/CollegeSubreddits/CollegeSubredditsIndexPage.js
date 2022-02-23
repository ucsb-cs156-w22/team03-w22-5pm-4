import React from 'react'
import { useBackend } from 'main/utils/useBackend';

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CollegeSubredditsTable from 'main/components/CollegeSubreddits/CollegeSubredditsTable';
import { useCurrentUser } from 'main/utils/currentUser'

export default function CollegeSubredditsIndexPage() {

  const currentUser = useCurrentUser();

  const { data: subreddits, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/collegesubreddits/all"],
      { method: "GET", url: "/api/collegesubreddits/all" },
      []
    );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>College Subreddits</h1>
        <CollegeSubredditsTable subreddits={subreddits} currentUser={currentUser} />
      </div>
    </BasicLayout>
  )
}