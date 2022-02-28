import React from "react";
import OurTable, { ButtonColumn } from "main/components/OurTable";

// import { useBackendMutation } from "main/utils/useBackend";
// import { cellToAxiosParamsPurge, onPurgeSuccess } from "main/utils/EarthquakeUtils"
// import { useNavigate } from "react-router-dom";
// import { hasRole } from "main/utils/currentUser";

export default function EarthquakesTable({ earthquakes, currentUser }) {

    // const purgeMutation = useBackendMutation(
    //     cellToAxiosParamsPurge,
    //     { onSuccess: onPurgeSuccess },
    //     ["/api/earthquakes/all"]
    // );
    // // Stryker enable all 

    // // Stryker disable next-line all : TODO try to make a good test for this
    // const purgeCallback = async (cell) => { purgeMutation.mutate(cell); }

    const columns = [
        {
            Header: 'id',
            accessor: '_id'
        },
        {
            Header: 'Title',
            accessor: 'properties.title', // accessor is the "key" in the data
        },
        {
            Header: 'Mag',
            accessor: 'properties.mag',
        },
        {
            Header: 'Place',
            accessor: 'properties.place',
        },
        {
            Header: 'Time',
            accessor: 'properties.time',
        }
    ];

    // if (hasRole(currentUser, "ROLE_ADMIN")) {
    //     // columns.push(ButtonColumn("Edit", "primary", editCallback, "EarthquakesTable"));
    //     columns.push(ButtonColumn("Purge", "danger", purgeCallback, "EarthquakesTable"));
    // } 

    // Stryker disable ArrayDeclaration : [columns] and [students] are performance optimization; mutation preserves correctness
    const memoizedColumns = React.useMemo(() => columns, [columns]);
    const memoizedDates = React.useMemo(() => earthquakes, [earthquakes]);
    // Stryker enable ArrayDeclaration

    return <OurTable
        data={memoizedDates}
        columns={memoizedColumns}
        testid={"EarthquakesTable"}
    />;
};