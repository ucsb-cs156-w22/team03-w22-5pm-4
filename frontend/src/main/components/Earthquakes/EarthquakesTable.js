import React from "react";
import OurTable from "main/components/OurTable";

export default function EarthquakesTable({ earthquakes, currentUser }) {

    const columns = [
        {
            Header: 'Title',
            accessor: 'title', // accessor is the "key" in the data
        },
        {
            Header: 'Mag',
            accessor: 'mag',
        },
        {
            Header: 'Place',
            accessor: 'place',
        },
        {
            Header: 'Time',
            accessor: 'time',
        }
    ];

    // if (hasRole(currentUser, "ROLE_ADMIN")) {
    //     columns.push(ButtonColumn("Edit", "primary", editCallback, "EarthquakesTable"));
    //     columns.push(ButtonColumn("Delete", "danger", deleteCallback, "EarthquakesTable"));
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