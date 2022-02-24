import React from 'react'
import { useBackend } from 'main/utils/useBackend';

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import UCSBSubjectsTable from 'main/components/UCSBSubjects/UCSBSubjectsTable';
import { useCurrentUser } from 'main/utils/currentUser'

export default function UCSBSubjectsIndexPage() {

  const currentUser = useCurrentUser();

  const { data: subjects, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/UCSBSubjects/all"],
      { method: "GET", url: "/api/UCSBSubjects/all" },
      []
    );

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>UCSBSubjects</h1>
        <UCSBSubjectsTable subjects={subjects} currentUser={currentUser} />
      </div>
    </BasicLayout>
  )
}