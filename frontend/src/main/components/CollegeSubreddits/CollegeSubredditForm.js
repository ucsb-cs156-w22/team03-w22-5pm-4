import React, { useState } from 'react'
import { Button, Form } from 'react-bootstrap';
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'


function CollegeSubredditForm({ initialCollegeSubreddit, submitAction, buttonLabel = "Create" }) {

    // Stryker disable all
    const {
        register,
        formState: { errors },
        handleSubmit,
    } = useForm(
        { defaultValues: initialCollegeSubreddit || {}, }
    );
    // Stryker enable all

    const navigate = useNavigate();

    // For explanation, see: https://stackoverflow.com/questions/3143070/javascript-regex-iso-datetime
    // Note that even this complex regex may still need some tweaks

    // Stryker disable next-line Regex
    //const isodate_regex = /(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d)/i;

    // Stryker disable next-line all
    //const yyyyq_regex = /((19)|(20))\d{2}[1-4]/i; // Accepts from 1900-2099 followed by 1-4.  Close enough.

    return (

        <Form onSubmit={handleSubmit(submitAction)}>

            {initialCollegeSubreddit && (
                <Form.Group className="mb-3" >
                    <Form.Label htmlFor="id">Id</Form.Label>
                    <Form.Control
                        data-testid="CollegeSubredditForm-id"
                        id="id"
                        type="text"
                        {...register("id")}
                        value={initialCollegeSubreddit.id}
                        disabled
                    />
                </Form.Group>
            )}

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="name">Name</Form.Label>
                <Form.Control
                    data-testid="CollegeSubredditForm-name"
                    id="name"
                    type="text"
                    isInvalid={Boolean(errors.name)}
                    {...register("name", {
                        required: "Name is required."
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.name?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="location">Location</Form.Label>
                <Form.Control
                    data-testid="CollegeSubredditForm-location"
                    id="location"
                    type="text"
                    isInvalid={Boolean(errors.location)}
                    {...register("location", {
                        required: "Location is required."
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.location?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="subreddit">subreddit</Form.Label>
                <Form.Control
                    data-testid="CollegeSubredditForm-subreddit"
                    id="subreddit"
                    type="text"
                    isInvalid={Boolean(errors.subreddit)}
                    {...register("subreddit", { required: "Subreddit is required." })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.subreddit?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Button
                type="submit"
                data-testid="CollegeSubredditForm-submit"
            >
                {buttonLabel}
            </Button>
            <Button
                variant="Secondary"
                onClick={() => navigate(-1)}
                data-testid="CollegeSubredditForm-cancel"
            >
                Cancel
            </Button>

        </Form>

    )
}

export default CollegeSubredditForm;
