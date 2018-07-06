/*******************************************************************************
 * Copyright (c) 2010-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.eclipse.scout.rt.client.ui.desktop;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.IDisplayParent;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.desktop.UnsavedFormChangesForm.MainBox.UnsavedChangesBox;
import org.eclipse.scout.rt.client.ui.desktop.UnsavedFormChangesForm.MainBox.UnsavedChangesBox.OpenFormsField;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.client.ui.wizard.IWizardContainerForm;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.classid.ClassId;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.ObjectUtility;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

@ClassId("b5783a42-9fd8-4043-afc1-6e744dad9c8f")
public class UnsavedFormChangesForm extends AbstractForm {
  private final List<IForm> m_forms;

  public UnsavedFormChangesForm(List<IForm> forms) {
    m_forms = forms;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("UnsavedChangesTitle");
  }

  @Override
  protected boolean getConfiguredAskIfNeedSave() {
    return false;
  }

  @Override
  protected IDisplayParent getConfiguredDisplayParent() {
    return getDesktop();
  }

  public void startNew() {
    startInternal(new NewHandler());
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public UnsavedChangesBox getUnsavedChangesBox() {
    return getFieldByClass(UnsavedChangesBox.class);
  }

  public OpenFormsField getOpenFormsField() {
    return getFieldByClass(OpenFormsField.class);
  }

  public List<IForm> getUnsavedForms() {
    return CollectionUtility.arrayList(m_forms);
  }

  @Order(10)
  @ClassId("7c89cc91-2c09-472b-af3b-ee93b50caaad")
  public class MainBox extends AbstractGroupBox {

    @Override
    protected int getConfiguredGridColumnCount() {
      return 2;
    }

    @Order(10)
    @ClassId("51908aa1-6409-44fd-9aeb-a92cec73baaa")
    public class UnsavedChangesBox extends AbstractGroupBox {

      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("SaveChangesOfSelectedItems");
      }

      @Override
      protected String getConfiguredBorderDecoration() {
        return BORDER_DECORATION_LINE;
      }

      @Override
      protected int getConfiguredGridColumnCount() {
        return 1;
      }

      @Order(20)
      @ClassId("84f2a9cf-bce5-4379-aede-11d07b21d3fb")
      public class OpenFormsField extends AbstractListBox<IForm> {

        @Override
        protected void execInitField() {
          checkAllKeys();
        }

        public List<IForm> getInvalidForms() {
          LinkedList<IForm> invalidForms = new LinkedList<>();
          for (IForm f : getValue()) {
            try {
              f.validateForm();
            }
            catch (RuntimeException e) { // NOSONAR
              invalidForms.add(f);
            }
          }
          return invalidForms;
        }

        @Override
        protected Class<? extends ILookupCall<IForm>> getConfiguredLookupCall() {
          return UnsavedFormsLookupCall.class;
        }

        @Override
        protected void execPrepareLookup(ILookupCall<IForm> call) {
          UnsavedFormsLookupCall unsavedFormsLookupCall = (UnsavedFormsLookupCall) call;
          unsavedFormsLookupCall.setUnsavedForms(getUnsavedForms());
        }

        @Override
        protected int getConfiguredGridH() {
          return 5;
        }

        @Override
        protected boolean getConfiguredLabelVisible() {
          return false;
        }

        @ClassId("ee7f1455-7da5-402b-98c0-c9d75a221595")
        public class Tree extends DefaultListBoxTable {
          @Order(10)
          @ClassId("00ee88b2-be05-418c-8b21-ad0b324ab78e")
          public class CheckAllMenu extends AbstractMenu {

            @Override
            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
              return CollectionUtility.hashSet(TableMenuType.EmptySpace);
            }

            @Override
            protected String getConfiguredText() {
              return TEXTS.get("CheckAll");
            }

            @Override
            protected void execAction() {
              checkAllKeys();
            }
          }

          @Order(20)
          @ClassId("94666a06-2f50-4242-b3ba-5a75252eaf44")
          public class UncheckAllMenu extends AbstractMenu {

            @Override
            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
              return CollectionUtility.hashSet(TableMenuType.EmptySpace);
            }

            @Override
            protected String getConfiguredText() {
              return TEXTS.get("UncheckAll");
            }

            @Override
            protected void execAction() {
              uncheckAllKeys();
            }
          }
        }
      }
    }

    @Order(20)
    @ClassId("caca3d68-b8cc-4cb0-a35c-5b8ccbcc3745")
    public class OkButton extends AbstractOkButton {
      @Override
      protected String getConfiguredTooltipText() {
        return TEXTS.get("SaveCheckedFormsAndShutdown");
      }
    }

    @Order(30)
    @ClassId("50c8526a-333f-4878-9876-b48f2b583d88")
    public class CancelButton extends AbstractCancelButton {
      @Override
      protected String getConfiguredTooltipText() {
        return TEXTS.get("CancelShutdownAndReturnToTheApplication");
      }
    }
  }

  public class NewHandler extends AbstractFormHandler {
    @Override
    protected void execPostLoad() {
      touch();
    }

    @Override
    protected boolean execValidate() {
      List<IForm> invalidForms = getOpenFormsField().getInvalidForms();
      if (!invalidForms.isEmpty()) {
        StringBuilder msg = new StringBuilder(TEXTS.get("FormsCannotBeSaved"));
        msg.append("\n\n");
        for (IForm f : invalidForms) {
          msg.append("- ").append(getFormDisplayName(f)).append("\n");
        }
        MessageBoxes.createOk().withHeader(TEXTS.get("NotAllCheckedFormsCanBeSaved")).withBody(msg.toString()).show();
        return false;
      }
      return true;
    }

    @Override
    protected void execStore() {
      for (IForm f : getOpenFormsField().getValue()) {
        if (f instanceof IWizardContainerForm) {
          ((IWizardContainerForm) f).getWizard().doFinish();
        }
        else {
          f.doOk();
        }
      }
    }
  }

  private static String getFormDisplayName(IForm f) {
    return StringUtility.join(" - ", ObjectUtility.nvl(f.getTitle(), f.getClass().getName()), f.getSubTitle());
  }

  @SuppressWarnings({"serial", "squid:S2057"})
  @ClassId("70052229-e6e5-43f3-bac5-cabe6e4525d3")
  public static class UnsavedFormsLookupCall extends LocalLookupCall<IForm> {
    private List<IForm> m_unsavedForms;

    public void setUnsavedForms(List<IForm> unsavedForms) {
      m_unsavedForms = unsavedForms;
    }

    @Override
    protected List<? extends ILookupRow<IForm>> execCreateLookupRows() {
      List<ILookupRow<IForm>> formRows = new ArrayList<>();
      for (IForm f : m_unsavedForms) {
        String text = getFormDisplayName(f);
        formRows.add(new LookupRow<>(f, text).withTooltipText(text));
      }
      return formRows;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((m_unsavedForms == null) ? 0 : m_unsavedForms.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!super.equals(obj)) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      UnsavedFormsLookupCall other = (UnsavedFormsLookupCall) obj;
      if (m_unsavedForms == null) {
        if (other.m_unsavedForms != null) {
          return false;
        }
      }
      else if (!m_unsavedForms.equals(other.m_unsavedForms)) {
        return false;
      }
      return true;
    }
  }
}