///*
// * Copyright 2013 JBoss by Red Hat.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.jbpm.console.ng.ht.client.editors.taskslist;
//
//import javax.inject.Inject;
//
//import org.jbpm.console.ng.ht.client.i18n.Constants;
//import org.jbpm.console.ng.ht.client.resources.HumanTasksImages;
//import org.jbpm.console.ng.ht.model.TaskSummary;
//import org.uberfire.security.Identity;
//
//import com.google.gwt.cell.client.ActionCell;
//import com.google.gwt.cell.client.Cell;
//import com.google.gwt.cell.client.FieldUpdater;
//import com.google.gwt.cell.client.HasCell;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.resources.client.ImageResource;
//import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
//import com.google.gwt.user.client.ui.AbstractImagePrototype;
//import com.google.gwt.user.client.ui.Composite;
//
//public abstract class ActionsCellTaskList extends Composite {
//    
//    protected Constants constants = GWT.create(Constants.class);
//    protected HumanTasksImages images = GWT.create(HumanTasksImages.class);
//    
//    @Inject
//    protected Identity identity;
//
//    protected class DetailsHasCell implements HasCell<TaskSummary, TaskSummary> {
//        private ActionCell<TaskSummary> cell;
//
//        public DetailsHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
//            cell = new ActionCell<TaskSummary>(text, delegate) {
//                @Override
//                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
//
//                    ImageResource detailsIcon = images.detailsIcon();
//                    AbstractImagePrototype imageProto = AbstractImagePrototype.create(detailsIcon);
//                    SafeHtmlBuilder mysb = new SafeHtmlBuilder();
//                    mysb.appendHtmlConstant("<span title='" + constants.Details() + "' style='margin-right:5px;'>");
//                    mysb.append(imageProto.getSafeHtml());
//                    mysb.appendHtmlConstant("</span>");
//                    sb.append(mysb.toSafeHtml());
//                }
//            };
//
//        }
//
//        @Override
//        public Cell<TaskSummary> getCell() {
//            return cell;
//        }
//
//        @Override
//        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
//            return null;
//        }
//
//        @Override
//        public TaskSummary getValue(TaskSummary object) {
//            return object;
//        }
//    }
//
//    protected class StartActionHasCell implements HasCell<TaskSummary, TaskSummary> {
//        private ActionCell<TaskSummary> cell;
//
//        public StartActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
//            cell = new ActionCell<TaskSummary>(text, delegate) {
//                @Override
//                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
//                    if (value.getActualOwner() != null
//                            && value.getActualOwner().equals(identity.getName())
//                            && (value.getStatus().equals("Reserved"))) {
//                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.startGridIcon());
//                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
//                        mysb.appendHtmlConstant("<span title='" + constants.Start() + "' style='margin-right:5px;'>");
//                        mysb.append(imageProto.getSafeHtml());
//                        mysb.appendHtmlConstant("</span>");
//                        sb.append(mysb.toSafeHtml());
//                    }
//                }
//            };
//        }
//
//        @Override
//        public Cell<TaskSummary> getCell() {
//            return cell;
//        }
//
//        @Override
//        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
//            return null;
//        }
//
//        @Override
//        public TaskSummary getValue(TaskSummary object) {
//            return object;
//        }
//    }
//
//    protected class CompleteActionHasCell implements HasCell<TaskSummary, TaskSummary> {
//        private ActionCell<TaskSummary> cell;
//
//        public CompleteActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
//            cell = new ActionCell<TaskSummary>(text, delegate) {
//                @Override
//                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
//                    if (value.getActualOwner() != null && value.getStatus().equals("InProgress")) {
//                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.completeGridIcon());
//                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
//                        mysb.appendHtmlConstant("<span title='" + constants.Complete() + "' style='margin-right:5px;'>");
//                        mysb.append(imageProto.getSafeHtml());
//                        mysb.appendHtmlConstant("</span>");
//                        sb.append(mysb.toSafeHtml());
//                    }
//                }
//            };
//        }
//
//        @Override
//        public Cell<TaskSummary> getCell() {
//            return cell;
//        }
//
//        @Override
//        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
//            return null;
//        }
//
//        @Override
//        public TaskSummary getValue(TaskSummary object) {
//            return object;
//        }
//    }
//
//    protected class ClaimActionHasCell implements HasCell<TaskSummary, TaskSummary> {
//        private ActionCell<TaskSummary> cell;
//
//        public ClaimActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
//            cell = new ActionCell<TaskSummary>(text, delegate) {
//                @Override
//                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
////                    if (value.getPotentialOwners() != null && !value.getPotentialOwners().isEmpty()
//                    if (value.getStatus().equals("Ready")) {
//                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.releaseGridIcon());
//                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
//                        mysb.appendHtmlConstant("<span title='" + constants.Claim() + "' style='margin-right:5px;'>");
//                        mysb.append(imageProto.getSafeHtml());
//                        mysb.appendHtmlConstant("</span>");
//                        sb.append(mysb.toSafeHtml());
//                    }
//                }
//            };
//        }
//
//        @Override
//        public Cell<TaskSummary> getCell() {
//            return cell;
//        }
//
//        @Override
//        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
//            return null;
//        }
//
//        @Override
//        public TaskSummary getValue(TaskSummary object) {
//            return object;
//        }
//    }
//
//    protected class ReleaseActionHasCell implements HasCell<TaskSummary, TaskSummary> {
//        private ActionCell<TaskSummary> cell;
//
//        public ReleaseActionHasCell(String text, ActionCell.Delegate<TaskSummary> delegate) {
//            cell = new ActionCell<TaskSummary>(text, delegate) {
//                @Override
//                public void render(Cell.Context context, TaskSummary value, SafeHtmlBuilder sb) {
////                    if (value.getPotentialOwners() != null && !value.getPotentialOwners().isEmpty()
//                    if (value.getActualOwner() != null && value.getActualOwner().equals(identity.getName())
//                            && (value.getStatus().equals("Reserved") || value.getStatus().equals("InProgress"))) {
//                        AbstractImagePrototype imageProto = AbstractImagePrototype.create(images.claimGridIcon());
//                        SafeHtmlBuilder mysb = new SafeHtmlBuilder();
//                        mysb.appendHtmlConstant("<span title='" + constants.Release() + "' style='margin-right:5px;'>");
//                        mysb.append(imageProto.getSafeHtml());
//                        mysb.appendHtmlConstant("</span>");
//                        sb.append(mysb.toSafeHtml());
//                    }
//                }
//            };
//        }
//
//        @Override
//        public Cell<TaskSummary> getCell() {
//            return cell;
//        }
//
//        @Override
//        public FieldUpdater<TaskSummary, TaskSummary> getFieldUpdater() {
//            return null;
//        }
//
//        @Override
//        public TaskSummary getValue(TaskSummary object) {
//            return object;
//        }
//    }
//    
//}
