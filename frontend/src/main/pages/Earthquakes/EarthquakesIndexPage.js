import React from 'react'
import { useBackend, useBackendMutation } from 'main/utils/useBackend';
import { Button } from 'react-bootstrap';
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import EarthquakesTable from 'main/components/Earthquakes/EarthquakesTable';
import { useCurrentUser } from 'main/utils/currentUser'
import { toast } from "react-toastify";
import { hasRole } from "main/utils/currentUser";

export default function EarthquakesIndexPage() {

  const currentUser = useCurrentUser();

  const { data: earthquakes, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/earthquakes/all"],
      { method: "GET", url: "/api/earthquakes/all" },
      []
    );

    function Purge_Button (){
      // Stryker disable next-line all : don't test internal caching of React Query
      if (hasRole(currentUser, "ROLE_ADMIN")) {
        const purge = useBackendMutation(
          () => ({ url: "/api/earthquakes/purge", method: "Delete" }),
          { onSuccess: () => { toast("Successfully deleted all earthquakes!"); } },
          // Stryker disable next-line all : don't test internal caching of React Query
          ["/api/earthquakes/all"],
        );
        return (
          <Button onClick={ () => { purge.mutate(); } } data-testid="purge-button">
            Purge
          </Button>)
      }
      return null;

    }
    return (
      <BasicLayout>
        <div className="pt-2">
          <h1>Earthquakes</h1>
          <EarthquakesTable earthquakes={earthquakes} currentUser={currentUser} />
          <Purge_Button/>
        </div>
      </BasicLayout>

    )
}