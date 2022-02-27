import React from 'react';

import EarthquakeForm from "main/components/Earthquakes/EarthquakeForm"
import { earthquakeFixtures } from 'fixtures/earthquakeFixtures';

export default {
    title: 'components/Earthquakes/EarthquakeForm',
    component: EarthquakeForm
};


const Template = (args) => {
    return (
        <EarthquakeForm {...args} />
    )
};

export const Default = Template.bind({});

Default.args = {
    submitText: "Create",
    submitAction: () => { console.log("Submit was clicked"); }
};

export const Show = Template.bind({});

Show.args = {
    earthquake: earthquakeFixtures.oneDate,
    submitText: "",
    submitAction: () => { }
};
