import * as AppFormer from "appformer-js";
import * as HomeApi from "kie-wb-common-home-api";
import {Profile} from "@kiegroup-ts-generated/kie-wb-common-profile-api";

export class JbpmWbHomeScreenProvider implements HomeApi.HomeScreenProvider {
    public get(profile: Profile): HomeApi.HomeScreen {
        const welcomeText = AppFormer.translate("Heading", []);
        const description = AppFormer.translate("SubHeading", []);
        const backgroundImageUrl = "images/home_bg.jpg";

        const cards = [this.designCard(), this.devOpsCard(), this.manageCard(), this.trackCard()];

        return new HomeApi.HomeScreen(welcomeText, description, backgroundImageUrl, cards);
    }

    private designCard() {
        const cssClasses = ["pficon", "pficon-blueprint"];
        const title = AppFormer.translate("Design", []);

        const descriptionTextMask = AppFormer.translate("DesignDescription", []);
        const description = new HomeApi.CardDescriptionBuilder(descriptionTextMask).build();

        return new HomeApi.Card(cssClasses, title, description, "LibraryPerspective");
    }

    private devOpsCard() {
        const cssClasses = ["fa", "fa-gears"];
        const title = AppFormer.translate("DevOps", []);

        const descriptionTextMask = AppFormer.translate("DevOpsDescription", []);
        const description = new HomeApi.CardDescriptionBuilder(descriptionTextMask).build();

        return new HomeApi.Card(cssClasses, title, description, "ServerManagementPerspective");
    }

    private manageCard() {
        const cssClasses = ["fa", "fa-briefcase"];
        const title = AppFormer.translate("Manage", []);

        const descriptionTextMask = AppFormer.translate("ManageDescription", []);
        const description = new HomeApi.CardDescriptionBuilder(descriptionTextMask).build();

        return new HomeApi.Card(cssClasses, title, description, "ProcessInstances");
    }

    private trackCard() {
        const cssClasses = ["pficon", "pficon-trend-up"];
        const title = AppFormer.translate("Track", []);

        const descriptionTextMask = AppFormer.translate("TrackDescription", []);
        const description = new HomeApi.CardDescriptionBuilder(descriptionTextMask).build();

        return new HomeApi.Card(cssClasses, title, description, "ProcessDashboardPerspective");
    }
}

AppFormer.register(new HomeApi.HomeScreenAppFormerComponent(new JbpmWbHomeScreenProvider()));
