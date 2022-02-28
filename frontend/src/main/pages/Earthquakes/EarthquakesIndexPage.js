import React from 'react'
import { useBackend } from 'main/utils/useBackend';
import { Button } from "react-bootstrap";


import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import EarthquakesTable from 'main/components/Earthquakes/EarthquakesTable';
import { useCurrentUser } from 'main/utils/currentUser'
import { toast } from 'react-toastify';

export default function EarthquakesIndexPage() {

  const currentUser = useCurrentUser();

  const { data: earthquakes, error: _error, status: _status } =
    useBackend(
      // Stryker disable next-line all : don't test internal caching of React Query
      ["/api/earthquakes/all"],
      { method: "GET", url: "/api/earthquakes/all" },
      []
    );

    // const objectToAxiosParams = (earthquake) => ({
    //   url: "/api/earthquakes/purge",
    //   method: "POST",
    // });

    // const onSuccess = (earthquake) => {
    //   toast(`All Earthquakes purged.`);
    // }

    // const mutation = useBackendMutation(
    //   objectToAxiosParams,
    //    { onSuccess }, 
    //    // Stryker disable next-line all : hard to set up test for caching
    //    ["/api/earthquakes/all"]
    //    );
  
    // const onSubmit = async (data) => {
    //   mutation.mutate(data);
    // }
    
  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Earthquakes</h1>
        <EarthquakesTable earthquakes={earthquakes} currentUser={currentUser} />
        <Button
        // type="submit"
        // onClick={onSubmit}
        // data-testid="EarthquakesList-purge"
        >
          Purge
        </Button>
      </div>
    </BasicLayout>
  )
}