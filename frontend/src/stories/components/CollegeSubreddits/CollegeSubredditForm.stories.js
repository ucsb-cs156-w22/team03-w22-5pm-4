import React from 'react';

import CollegeSubredditForm from "main/components/CollegeSubreddits/CollegeSubredditForm"
import { collegeSubredditsFixtures } from 'fixtures/collegeSubredditsFixtures';

export default {
    title: 'components/CollegeSubreddits/CollegeSubredditForm',
    component: CollegeSubredditForm
};


const Template = (args) => {
    return (
        <UCSBDateForm {...args} />
    )
};

export const Default = Template.bind({});

Default.args = {
    submitText: "Create",
    submitAction: () => { console.log("Submit was clicked"); }
};

export const Show = Template.bind({});

Show.args = {
    ucsbDate: collegeSubredditsFixtures.oneDate,
    submitText: "",
    submitAction: () => { }
};
