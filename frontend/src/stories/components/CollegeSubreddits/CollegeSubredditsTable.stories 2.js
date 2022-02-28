import React from 'react';

import CollegeSubredditsTable from "main/components/CollegeSubreddits/CollegeSubredditsTable";
import { collegeSubredditsFixtures } from 'fixtures/collegeSubredditsFixtures';

export default {
    title: 'components/CollegeSubreddits/CollegeSubredditsTable',
    component: CollegeSubredditsTable
};

const Template = (args) => {
    return (
        <CollegeSubredditsTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    reddits: []
};

export const ThreeReddits = Template.bind({});

ThreeDates.args = {
    reddits: collegeSubredditsFixtures.threeReddits
};


